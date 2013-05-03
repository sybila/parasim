# provides function parasim.robustColor which returns color for point of given robustness in the color schema similar to that of basic plotter
#
# parasim.robustColor(robustness, maxVal=50)
#	returns color (i.e. "#00FF00")
#	maxValue	... maximum absolute value of robustness (robustness has to be normalized)
#					should be fixed for one result (or fore more results when they are compared))
#	robustness	... robustness of given point
#

parasim.color.redRamp <- colorRamp(c("#FF9900","#770000"))
parasim.color.greenRamp <- colorRamp(c("#99FF00","#007700"))

parasim.robustColor <- function(robustness,maxVal=50) rgb((if(robustness>0){parasim.color.greenRamp(robustness/maxVal)}else{parasim.color.redRamp(-robustness/maxVal)}),maxColorValue=255)

#
# Can be used to plot diagrams similar to that of basic plot (looks way nicer when done in R):
#
#	input <- read.csv("input_file.csv", sep="\t")
#	input$color <- mapply(parasim.robustColor, input$Robustness)
#	plot(input$x_variable, input$y_variable, pch=20, col=input$color)
#
