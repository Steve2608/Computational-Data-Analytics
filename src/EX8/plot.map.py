from scipy.io import arff
import pandas as pd
import matplotlib.pyplot as plt

data = arff.loadarff('cities_greece.arff')
df = pd.DataFrame(data[0])

plt.scatter(df.Longitude, df.Latitude, marker='.', s=2, alpha=0.2)
plt.axis('off')
plt.savefig('greece.png', dpi=200)