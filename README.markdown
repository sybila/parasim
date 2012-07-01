# Parasim

## Packaging, Installation, Deployment

From the root directory:

    mvn clean [deploy|install|package] -P[applications|core|extensions|license|model|parent|util]

## Generate License Headers

Go to the module where you want to update license headers:

    mvn clean validate -Pgenerate-license