import json, os, shutil, io, base64
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.tree import DecisionTreeClassifier

def ensure_json_in_filesdir(files_dir):
    src_json = os.path.join(os.path.dirname(__file__), "dataset.json")
    dst_json = os.path.join(files_dir, "dataset.json")
    if not os.path.exists(dst_json):
        shutil.copy(src_json, dst_json)
    return dst_json

def load_and_clean_from_json(json_path):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

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
            dificulty = game.get("dificulty", "unknown")

            rows.append({
                "age": age,
                "errors": errors,
                "gameTime": game_time,
                "points": points,
                "avgReaction": avg_reaction,
                "name": name,
                "dificulty": dificulty
            })

    df = pd.DataFrame(rows)
    df = df[df["gameTime"] > 0]
    df.drop_duplicates(inplace=True)
    return df

def fig_to_base64():
    buf = io.BytesIO()
    plt.savefig(buf, format="png")
    plt.close()
    buf.seek(0)
    return base64.b64encode(buf.read()).decode("utf-8")

def train(output_dir):
    json_path = ensure_json_in_filesdir(output_dir)
    df = load_and_clean_from_json(json_path)

    # Entrenar modelo (igual que antes)
    X = df[["age", "errors", "gameTime", "avgReaction"]]
    y = (df["points"] > 0).astype(int)
    clf = DecisionTreeClassifier()
    clf.fit(X, y)
    global model
    model = clf

    graphs = {}

    # 1️⃣ Histograma de tiempos de juego
    plt.figure(figsize=(6,4))
    df["gameTime"].hist(bins=10, color="skyblue", edgecolor="black")
    plt.title("Distribución de tiempos de juego")
    plt.xlabel("Tiempo de juego")
    plt.ylabel("Frecuencia")
    graphs["hist_gameTime"] = fig_to_base64()

    # 2️⃣ Promedio de puntos por jugador
    plt.figure(figsize=(6,4))
    df.groupby("name")["points"].mean().plot(kind="bar", color="orange")
    plt.title("Promedio de puntos por jugador")
    plt.ylabel("Puntos promedio")
    graphs["points_per_player"] = fig_to_base64()

    # 3️⃣ Errores por dificultad
    plt.figure(figsize=(6,4))
    df.groupby("dificulty")["errors"].mean().plot(kind="bar", color="red")
    plt.title("Errores promedio por dificultad")
    plt.ylabel("Errores promedio")
    graphs["errors_per_difficulty"] = fig_to_base64()

    # 4️⃣ Tiempo de reacción promedio por jugador
    plt.figure(figsize=(6,4))
    df.groupby("name")["avgReaction"].mean().plot(kind="bar", color="green")
    plt.title("Tiempo de reacción promedio por jugador")
    plt.ylabel("Segundos")
    graphs["reaction_per_player"] = fig_to_base64()

    # 5️⃣ Relación tiempo de juego vs puntos
    plt.figure(figsize=(6,4))
    plt.scatter(df["gameTime"], df["points"], color="purple")
    plt.title("Relación tiempo de juego vs puntos")
    plt.xlabel("Tiempo de juego")
    plt.ylabel("Puntos")
    graphs["scatter_gameTime_points"] = fig_to_base64()

    return graphs