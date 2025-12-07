import json
import os
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.tree import DecisionTreeClassifier
from sklearn import tree
import io, base64

# Configuración
plt.switch_backend('Agg')  # No usar GUI
IMAGE_DIR = "/data/data/com.example.myapplication/files/graphs"

def log(msg):
    print(f"[PY_DEBUG] {msg}")

# -----------------------------
# 1. Cargar datos
# -----------------------------
def load_data():
    """Carga datos del JSON desde posibles rutas"""
    log("Cargando datos...")
    paths = [
        "/data/data/com.example.myapplication/files/dataset.json",
        "/data/user/0/com.example.myapplication/files/dataset.json",
        "dataset.json"
    ]
    for path in paths:
        try:
            with open(path, "r", encoding="utf-8") as f:
                data = json.load(f)
            log(f"Datos cargados de: {path}")
            return data
        except:
            continue
    raise Exception("No se pudo cargar dataset.json")

# -----------------------------
# 2. Procesar datos
# -----------------------------
def process_data(data):
    """Convierte datos JSON a DataFrame"""
    rows = []
    for cat in data["cat"]:
        for game in cat["gameList"]:
            rows.append({
                "age": cat.get("age", -1),
                "name": cat.get("name", "unknown"),
                "errors": game.get("errors", 0),
                "gameTime": game.get("gameTime", 0),
                "points": game.get("points", 0),
                "avgReaction": sum(game.get("reactionTime", []))/len(game.get("reactionTime", [])) 
                              if game.get("reactionTime") else -1,
                "dificulty": game.get("dificulty", "unknown")
            })
    df = pd.DataFrame(rows)
    log(f"DataFrame creado: {len(df)} filas (incluyendo gameTime=0)")
    return df

# -----------------------------
# 3. Entrenar modelo
# -----------------------------
def train_model(df):
    """Entrena un árbol de decisión con los datos"""
    X = df[["age", "errors", "gameTime", "avgReaction"]]
    y = (df["points"] > 0).astype(int)

    clf = DecisionTreeClassifier(max_depth=None, random_state=42)
    clf.fit(X, y)

    log(f"Modelo entrenado con {len(df)} registros")
    log(f"Distribución de clases:\n{y.value_counts()}")

    return clf

# -----------------------------
# 4. Utilidad para convertir figuras a Base64
# -----------------------------
def fig_to_base64(fig):
    buf = io.BytesIO()
    fig.savefig(buf, format="png")
    buf.seek(0)
    return base64.b64encode(buf.read()).decode("utf-8")

# -----------------------------
# 5. Generar gráficos
# -----------------------------
def generate_graphs(df, clf):
    """Genera gráficos y devuelve en Base64"""
    results = {}

    # Gráfico 1: Puntos promedio por jugador
    fig1 = plt.figure(figsize=(10, 6))
    df.groupby("name")["points"].mean().plot(kind="bar", color="skyblue")
    plt.title("Puntos Promedio por Jugador")
    plt.ylabel("Puntos")
    plt.xlabel("Jugador")
    plt.xticks(rotation=45)
    plt.tight_layout()
    results["graph1"] = fig_to_base64(fig1)
    plt.close(fig1)

    # Gráfico 2: Dispersión tiempo vs puntos
    fig2 = plt.figure(figsize=(10, 6))
    plt.scatter(df["gameTime"], df["points"], alpha=0.6, color="green")
    plt.title("Tiempo de Juego vs Puntos")
    plt.xlabel("Tiempo")
    plt.ylabel("Puntos")
    plt.grid(alpha=0.3)
    plt.tight_layout()
    results["graph2"] = fig_to_base64(fig2)
    plt.close(fig2)

    # Gráfico 3: Árbol de decisión
    fig3 = plt.figure(figsize=(22, 14))
    tree.plot_tree(
        clf,
        feature_names=["age", "errors", "gameTime", "avgReaction"],
        class_names=["sin puntos", "con puntos"],
        filled=True,
        rounded=True
    )
    plt.title("Árbol de Decisión (profundidad extendida)")
    plt.tight_layout()
    results["graph3"] = fig_to_base64(fig3)
    plt.close(fig3)

    log(f"{len(results)} gráficos generados")
    return results

# -----------------------------
# 6. Función principal
# -----------------------------
def create_graphs():
    log("=== CREANDO GRÁFICOS ===")
    try:
        data = load_data()
        df = process_data(data)
        clf = train_model(df)
        results = generate_graphs(df, clf)
        return results
    except Exception as e:
        log(f"ERROR: {str(e)}")
        return {"error": str(e)}