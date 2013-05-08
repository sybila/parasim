library("rgl");

parasim.color.redRamp <- colorRamp(c("#FF9900","#770000"))
parasim.color.greenRamp <- colorRamp(c("#99FF00","#007700"))

parasim.robustColor <- function(robustness, robustness.max = NULL, robustness.min = NULL) {
	if (is.null(robustness.max)) {
		robustness.max <- max(robustness)
	}
	if (robustness.max == 0) {
		robustness.max <- 1;
	}
	if (is.null(robustness.min)) {
		robustness.min <- min(robustness)		
	}
	if (robustness.min == 0) {
		robustness.min <- -1;
	}
	mapply(
		function(x) {
			col <- if (x > 0) {
				parasim.color.greenRamp(x/robustness.max)
			} else {
				parasim.color.redRamp(abs(x/robustness.min))
			}
			rgb(col, maxColorValue=255)
		},
		robustness);
}

parasim.plot.data <- function(x, y, robustness, z = NULL, use.3d = TRUE, ...) {
	if (use.3d || !is.null(z)) {
		ok.filter <- robustness > 0;
		nok.filter <- robustness <= 0;
		if (is.null(z)) {
			ok.data <- data.frame(x = x[ok.filter], y = y[ok.filter], robustness = robustness[ok.filter]);
			nok.data <- data.frame(x = x[nok.filter], y = y[nok.filter], robustness = robustness[nok.filter]);
		} else {
			ok.data <- data.frame(x = x[ok.filter], y = y[ok.filter], z = z[ok.filter]);
			nok.data <- data.frame(x = x[nok.filter], y = y[nok.filter], z = z[nok.filter]);
		}
		plot3d(ok.data, col="green", ...);
		plot3d(nok.data, col="red", add=TRUE, ...);
	} else {
		plot(x, y, col = parasim.robustColor(robustness), pch=20, ...);
	}
}

parasim.plot.csv <- function(file, x.name, y.name, z.name = NULL, robustness.name = "Robustness", use.3d = TRUE, ...) {
	data <- read.table(file, header=TRUE);
	if (!is.null(z.name)) {
		parasim.plot.data(data[[x.name]], data[[y.name]], data[[robustness.name]], z = data[[z.name]], use.3d = use.3d, ...);
	} else {
		parasim.plot.data(data[[x.name]], data[[y.name]], data[[robustness.name]], use.3d = use.3d, ...);
	}
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
