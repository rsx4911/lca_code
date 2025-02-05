import dash
from dash import dcc, html
import plotly.express as px
import pandas as pd

# Initialize Dash app
app = dash.Dash(__name__)

# Dataset Usage Guidelines (similar to your React `usageGuidelines`)
data = pd.DataFrame({
    "Category": ["Basic", "Attribution", "Open Access", "Unrestricted", "Technical"],
    "Count": [10131, 8352, 4047, 2424, 725],
    "Description": [
        "Datasets with no specific usage guidelines. While no special recommendations are provided, general CC0 principles apply.",
        "These datasets include a recommendation to credit the data owner and LCA Commons. While not required under CC0, attribution supports scientific transparency.",
        "Datasets marked as openly accessible to all users, including LCA practitioners, industry, and the general public.",
        "Datasets explicitly marked as having no restrictions, reinforcing the CC0 dedication.",
        "Includes additional technical documentation and usage guidance for optimal data utilization."
    ]
})

# Create Bar Chart with Hover Information
fig = px.bar(
    data,
    x="Category",
    y="Count",
    text="Count",
    labels={"Count": "Number of Datasets"},
    title="Dataset Usage Guidelines Distribution",
    color="Category",
    hover_data={"Description": True}  # This enables tooltips on hover
)

fig.update_traces(textposition="outside")

# Metadata Fields Data (Table)
metadata_fields = [
    {"Field": "Description", "Purpose": "Provides an overview of the dataset", "Importance": "Helps users understand dataset contents, methodology, and scope"},
    {"Field": "Reviewer", "Purpose": "Identifies the person or entity that reviewed the dataset", "Importance": "Enhances credibility and ensures that the dataset meets quality standards"},
    {"Field": "Publication", "Purpose": "Reference to a related publication", "Importance": "Aids in scientific citation and reproducibility"},
    {"Field": "Usage Constraints", "Purpose": "Defines technical requirements and data limitations", "Importance": "Helps users determine if the dataset meets their needs"},
    {"Field": "Quality Flags", "Purpose": "Notes on known data limitations", "Importance": "Alerts users to potential data issues before use"},
    {"Field": "Access and Use Restrictions", "Purpose": "Defines restrictions on dataset usage", "Importance": "Ensures compliance with CC0 while clarifying any advisory restrictions"},
    {"Field": "Attribution", "Purpose": "Recommends citing the source", "Importance": "Encourages best practices in scientific research, even though CC0 1.0 does not require it"}
]

# Why Metadata Matters Section
metadata_importance = [
    "Increases Dataset Usability - Metadata provides essential context to users, enabling them to understand the dataset's purpose, limitations, and appropriate usage.",
    "Enhances Scientific Reproducibility - Clear metadata ensures that researchers can replicate findings, strengthening the dataset's validity.",
    "Improves Data Discoverability - Well-documented datasets are easier to find and integrate into other studies and systems.",
    "Ensures Compliance with Open Data Standards - Proper metadata aligns datasets with open science principles.",
    "Reduces Misinterpretation and Errors - Missing or unclear metadata increases the risk of misuse and poor decision-making."
]

# Add Metadata Fields Table
metadata_table = html.Div([
    html.H3("Key Metadata Fields and Their Importance"),
    dash.dash_table.DataTable(
        columns=[
            {"name": "Field", "id": "Field"},
            {"name": "Purpose", "id": "Purpose"},
            {"name": "Importance", "id": "Importance"}
        ],
        data=metadata_fields,
        style_table={'overflowX': 'auto'},
        style_cell={'textAlign': 'left', 'padding': '10px'},
        style_header={'backgroundColor': '#f2f2f2', 'fontWeight': 'bold'}
    )
])

# Add Why Metadata Matters Section
metadata_importance_section = html.Div([
    html.H3("Why Improving Metadata Practices and Documentation Matters"),
    html.Ul([html.Li(point) for point in metadata_importance], style={"font-size": "14px"})
])

# Layout
app.layout = html.Div(children=[
    html.H1("LCA Commons Dataset Analysis Dashboard"),
    html.P("Total Repositories: 50 | Total Datasets: 26,977"),
    
    html.Div([
        html.Div("All Datasets are provided under Creative Commons CC0 1.0 Universal Public Domain Dedication. The Categories shown below reflect recommended practices and legacy metadata tags, not legal restrictions. User have complete freedom to use, modify, and share all data."),
    ], style={"backgroundColor": "#fff3cd", "padding": "10px", "borderLeft": "5px solid #ffc107"}),

    dcc.Graph(figure=fig),  # Render Bar Chart with hover information


    html.H3("Common Metadata Issues"),
    html.Ul([
        html.Span("Missing description field: 26,977", style={"font-weight":"bold"}),
        html.P("No dataset description provided, reducing clarity on data scope and intent.", style={"font-size": "16px", "color": "gray"})
    ]),

    html.Ul([    
        html.Span("Missing reviewer field: 16,295", style={"font-weight":"bold"}),
        html.P("No reviewer listed, making it diffilcult to verify credibility.", style={"font-size": "16px", "color": "gray"})
    ]),

    html.Ul([
        html.Span("Missing publication field: 7,820", style={"font-weight":"bold"}),
        html.P("No publication reference. This may limit the ability to trace dataset origin.", style={"font-size": "16px", "color": "gray"})
    ]),

    html.H3("Recommended Best Practices"),
    html.Ul([
        html.Li("Make sure datasets have a clear description"),
        html.Li("Include citations in academic work"),
        html.Li("Review any technical recommendations"),
        html.Li("Consider documented quality flags"),
        html.Li("Document modifications"),
        html.Li("Check publication and reviewer fields for improved documentation"),
    ]),

    # Metadata Fields Table
    metadata_table,
    # Why Metadata Matters Section
    metadata_importance_section
    
])

# Run server
if __name__ == "__main__":
    app.run_server(debug=True)

# Dashboard web server: http://127.0.0.1:8050/
