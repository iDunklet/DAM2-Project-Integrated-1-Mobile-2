# script.py
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import pandas as pd

def get_hello():
    return "Hello World"

def test_imports():
    try:
        # Datos de prueba simples
        X = [[1, 2], [2, 3], [3, 4]]
        y = [0, 1, 0]

        modelo = DecisionTreeClassifier()
        modelo.fit(X, y)

        return "✅ Todas las importaciones funcionan correctamente"
    except Exception as e:
        return f"❌ Error: {str(e)}"