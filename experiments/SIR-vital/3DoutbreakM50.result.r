library("rgl");
data <- read.table("3DoutbreakM50.result.csv", header=TRUE);
ok <- subset(data, data$Robustness > 0);
nok <- subset(data, data$Robustness <= 0);
plot3d(ok[c(4,5,6)], col="green");
plot3d(nok[c(4,5,6)], col="red", add=TRUE);
