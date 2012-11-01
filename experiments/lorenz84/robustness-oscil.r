library("rgl");
data <- read.table("result-oscil.csv", header=TRUE);
data <- data[c(4,5,6)];
ok <- subset(data, data$Robustness > 0);
nok <- subset(data, data$Robustness <= 0);
plot3d(ok, col="green");
plot3d(nok, col="red", add=TRUE);
