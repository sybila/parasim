library("rgl");
data <- read.table("simple.result.csv", header=TRUE);
ok <- subset(data, data$Robustness > 0);
nok <- subset(data, data$Robustness <= 0);
plot3d(ok[c(6,7,8)], col="green");
plot3d(nok[c(6,7,8)], col="red", add=TRUE);
