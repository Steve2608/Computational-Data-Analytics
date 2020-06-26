import plotly.graph_objs as go

no_weighting = [91.2, 91.096, 92.008, 92.108, 92.336, 92.416, 92.512, 92.584, 92.556, 92.544, 92.536, 92.596, 92.572,
                92.56, 92.592, 92.608, 92.564, 92.556, 92.548, 92.556, 92.504, 92.544, 92.472, 92.436, 92.468, 92.396,
                92.396, 92.384, 92.388, 92.368, 92.404, 92.352, 92.396, 92.408, 92.36, 92.344, 92.332, 92.32, 92.324,
                92.328, 92.3, 92.3, 92.296, 92.284, 92.252, 92.276, 92.272, 92.256, 92.212, 92.232]

inverse_weighting = [91.2, 91.2, 91.912, 92.104, 92.384, 92.5, 92.556, 92.564, 92.58, 92.58, 92.564, 92.636, 92.624,
                     92.62, 92.604, 92.632, 92.572, 92.612, 92.528, 92.56, 92.54, 92.572, 92.46, 92.456, 92.444, 92.456,
                     92.392, 92.42, 92.416, 92.42, 92.428, 92.432, 92.44, 92.404, 92.412, 92.372, 92.38, 92.352, 92.38,
                     92.348, 92.328, 92.352, 92.336, 92.344, 92.3, 92.292, 92.32, 92.272, 92.276, 92.256]

similarity_weighting = [91.2, 91.2, 91.908, 92.104, 92.38, 92.512, 92.548, 92.556, 92.58, 92.576, 92.564, 92.64, 92.62,
                        92.612, 92.596, 92.64, 92.568, 92.6, 92.528, 92.552, 92.536, 92.572, 92.452, 92.46, 92.444,
                        92.44, 92.384, 92.412, 92.404, 92.4, 92.412, 92.412, 92.428, 92.384, 92.408, 92.36, 92.368,
                        92.34, 92.372, 92.336, 92.328, 92.34, 92.328, 92.336, 92.28, 92.3, 92.324, 92.28, 92.272,
                        92.248]

assert len(no_weighting) == len(inverse_weighting) == len(similarity_weighting)

fig = go.Figure()

fig.add_trace(go.Scatter(
    name='No distance weighting',
    x=list(range(1, len(no_weighting) + 1)),
    y=no_weighting,
    mode='markers+lines',
    hovertemplate='k=%{x}<br>accuracy=%{y}'
))

fig.add_trace(go.Scatter(
    name='Inverse weighting',
    x=list(range(1, len(inverse_weighting) + 1)),
    y=inverse_weighting,
    mode='markers+lines',
    hovertemplate='k=%{x}<br>accuracy=%{y}'
))

fig.add_trace(go.Scatter(
    name='Similarity weighting',
    x=list(range(1, len(similarity_weighting) + 1)),
    y=similarity_weighting,
    mode='markers+lines',
    hovertemplate='k=%{x}<br>accuracy=%{y}'
))

fig.update_layout(showlegend=True,
                  hovermode='x',
                  xaxis_title='neighbours',
                  xaxis=dict(
                      dtick=1,
                      tickangle=0,
                      tick0=0
                  ),
                  yaxis_title='accuracy in %',
                  title_text='Accuracies for KDTree')
fig.show()
