library("rgl");

parasim.plot.data <- function(x, y, robustness, ...) {
	ok.filter <- robustness > 0;
	nok.filter <- robustness <= 0;
	ok.data <- data.frame(x = x[ok.filter], y = y[ok.filter], robustness[ok.filter]);
	nok.data <- data.frame(x = x[nok.filter], y = y[nok.filter], robustness[nok.filter]);
	plot3d(ok.data, col="green", ...);
	plot3d(nok.data, col="red", add=TRUE, ...);
}

parasim.plot.csv <- function(file, x.name, y.name, robustness.name = "Robustness", ...) {
	data <- read.table(file, header=TRUE);
	parasim.show.data(data[x.name], data[y.name], data[robustness.name], ...);
}

parasim.robustness.future <- function(window.start, window.end, signal) {
	if (window.start > window.end) {
		simpleError("window start has to be lower than window end");
	}
	len <- length(signal);
	if (window.end > len) {
		simpleError("signal length is not sufficient");
	}
	result <- c()
	for (i in window.start:(len - window.end)) {
		result <- c(result, max(signal[i:(i-1+window.end)]))
	}
	result
}

parasim.robustness.globally <- function(window.start, window.end, signal) {
	if (window.start > window.end) {
		simpleError("window start has to be lower than window end");
	}
	len <- length(signal);
	if (window.end > len) {
		simpleError("signal length is not sufficient");
	}
	result <- c()
	for (i in window.start:(len - window.end)) {
		result <- c(result, min(signal[i:(i-1+window.end)]))
	}
	result
}

parasim.robustness.and <- function(x, y) {
	len <- min(length(x), length(y))
	result <- c()
	for(i in 1:len) {
		result <- c(result, min(x[i], y[i]))
	}
	result
}

parasim.robustness.neg <- function(signal) {
	-signal
}

parasim.robustness.or <- function(x, y) {
	parasim.robustness.neg(parasim.robustness.and(parasim.robustness.neg(x), parasim.robustness.neg(y)))
}

parasim.plot.robustness <- function(x, y, robustness, ...) {
	plot(x, y, pch = ".", ...)
	segments(x[1:length(robustness)], rep(0, length(robustness)), x[1:length(robustness)], robustness, col=ifelse(robustness > 0, "green", "red"))
	lines(x, y, ...)
}
