library("rgl");
data <- read.table("oscil.result.csv", header=TRUE);
ok <- subset(data, data$Robustness > 0);
nok <- subset(data, data$Robustness <= 0);
plot3d(ok, col="green");
plot3d(nok, col="red", add=TRUE);
