import json
import pandas as pd
from sklearn.tree import DecisionTreeClassifier
import matplotlib.pyplot as plt
import os
import shutil

# ===============================================================
# 1️⃣ Copiar JSON a filesDir
# ===============================================================
def ensure_json_in_filesdir(files_dir):
    src_json = os.path.join(os.path.dirname(__file__), "dataset.json")  # tu JSON original en app/src/main/python
    dst_json = os.path.join(files_dir, "dataset.json")                  # copia en almacenamiento interno
    if not os.path.exists(dst_json):
        shutil.copy(src_json, dst_json)
    return dst_json

# ===============================================================
# 2️⃣ CARGA Y LIMPIEZA
# ===============================================================
def load_and_clean_from_json(json_path):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    # Transformar la estructura en DataFrame
    rows = []
    for cat in data["cat"]:
        age = cat["age"]
        name = cat["name"]
        for game in cat["gameList"]:
            errors = game.get("errors", 0)
            game_time = game.get("gameTime", 0)
            points = game.get("points", 0)
            reaction = game.get("reactionTime", [])
            avg_reaction = sum(reaction)/len(reaction) if reaction else -1

            rows.append({
                "age": age,
                "errors": errors,
                "gameTime": game_time,
                "points": points,
                "avgReaction": avg_reaction,
                "name": name
            })

    df = pd.DataFrame(rows)

    # Limpieza básica
    df = df[df["gameTime"] > 0]
    df.drop_duplicates(inplace=True)

    return df

# ===============================================================
# 3️⃣ ENTRENAMIENTO
# ===============================================================
def train(output_dir):
    # Copiar JSON a filesDir y cargarlo
    json_path = ensure_json_in_filesdir(output_dir)
    df = load_and_clean_from_json(json_path)

    # Features y etiqueta
    X = df[["age", "errors", "gameTime", "avgReaction"]]
    y = (df["points"] > 0).astype(int)

    clf = DecisionTreeClassifier()
    clf.fit(X, y)

    global model
    model = clf

    # Crear gráfico
    plt.figure(figsize=(6,4))
    df["gameTime"].hist(bins=10, color="skyblue", edgecolor="black")
    plt.title("Distribución de tiempos de juego")
    plt.xlabel("Tiempo de juego")
    plt.ylabel("Frecuencia")

    # Guardar en data/data/<paquete>/files
    output_path = os.path.join(output_dir, "gameTime_hist.png")
    plt.savefig(output_path)
    plt.close()

    return f"Modelo entrenado con {len(df)} registros. Imagen guardada en {output_path}"

