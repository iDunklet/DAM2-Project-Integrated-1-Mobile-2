import json
import os
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.tree import DecisionTreeClassifier
from sklearn import tree
from sklearn.metrics import confusion_matrix   # ✅ añadido
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
    """Convierte datos JSON a DataFrame y aplica limpieza básica"""
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

    #Limipeza
    df = df.drop_duplicates()
    df = df.dropna(subset=["name", "gameTime", "points"])
    df["avgReaction"] = df["avgReaction"].fillna(-1)
    df["errors"] = df["errors"].fillna(0)
    df = df[df["gameTime"] > 0]
    df = df[df["errors"] >= 0]
    df = df[df["points"] >= 0]
    df["gameTime_min"] = df["gameTime"] / 60.0

    return df

# -----------------------------
# Función para matriz de confusión
# -----------------------------
def generate_confusion_matrix_image(y_true, y_pred):
    cm = confusion_matrix(y_true, y_pred)
    labels = ["No puntos", "Con puntos"]

    fig = plt.figure(figsize=(6, 5))
    plt.imshow(cm, cmap="Blues")
    plt.title("Matriz de Confusión")
    plt.colorbar()

    plt.xticks([0, 1], labels, rotation=45)
    plt.yticks([0, 1], labels)

    # números dentro de la matriz
    for i in range(cm.shape[0]):
        for j in range(cm.shape[1]):
            plt.text(j, i, cm[i, j], ha="center", va="center", color="black")

    plt.xlabel("Predicción")
    plt.ylabel("Real")
    plt.tight_layout()

    img_b64 = fig_to_base64(fig)
    plt.close(fig)
    return img_b64

# -----------------------------
# 3. Entrenar modelo
# -----------------------------
def train_model(df):
    """Entrena un árbol de decisión con los datos"""
    X = df[["age", "errors", "gameTime", "avgReaction"]]
    y = (df["points"] > 0).astype(int)

    clf = DecisionTreeClassifier(max_depth=None, random_state=42)
    clf.fit(X, y)

    y_pred = clf.predict(X)

    log(f"Modelo entrenado con {len(df)} registros")
    log(f"Distribución de clases:\n{y.value_counts()}")

    return clf, y, y_pred   

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
def generate_graphs(df, clf, y, y_pred):
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

    # Gráfico 4: Matriz de confusión
    results["confusion_matrix"] = generate_confusion_matrix_image(y, y_pred)

    #histograma
    log(f"{len(results)} gráficos generados")
    fig = plt.figure(figsize=(10, 6))
    plt.hist(df["gameTime"], bins=20, color="orange", edgecolor="black")
    plt.title("Distribución del Tiempo de Juego")
    plt.xlabel("Tiempo de juego")
    plt.ylabel("Frecuencia")
    plt.tight_layout()

    results["hist_gameTime"] = fig_to_base64(fig)
    plt.close(fig)

    #  Histograma de errores
    fig_err = plt.figure(figsize=(10, 6))
    plt.hist(df["errors"], bins=15, color="red", edgecolor="black")
    plt.title("Distribución de Errores")
    plt.xlabel("Errores")
    plt.ylabel("Frecuencia")
    plt.tight_layout()

    results["hist_errors"] = fig_to_base64(fig_err)
    plt.close(fig_err)

    # ✅ Heatmap de correlación
    fig_corr = plt.figure(figsize=(8, 6))
    corr = df[["age", "errors", "gameTime", "avgReaction", "points"]].corr()

    plt.imshow(corr, cmap="coolwarm", interpolation="nearest")
    plt.colorbar()
    plt.xticks(range(len(corr.columns)), corr.columns, rotation=45)
    plt.yticks(range(len(corr.columns)), corr.columns)
    plt.title("Mapa de Correlación entre Variables")
    plt.tight_layout()

    results["heatmap_corr"] = fig_to_base64(fig_corr)
    plt.close(fig_corr)



    return results

# -----------------------------
# 6. Función principal
# -----------------------------
def create_graphs():
    log("=== CREANDO GRÁFICOS ===")
    try:
        data = load_data()
        df = process_data(data)
        clf, y, y_pred = train_model(df)   # ✅ recibe y_pred
        results = generate_graphs(df, clf, y, y_pred)
        return results
    except Exception as e:
        log(f"ERROR: {str(e)}")
        return {"error": str(e)}