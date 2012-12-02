IMAGEEXT=png jpg
.PHONY: pdf images
pdf:
	for FILE in *.tex; do \
		NAME=`basename $$FILE .tex`; \
		pdflatex -shell-escape $$FILE; \
		convert -trim -bordercolor white -density 300 $$NAME.pdf -quality 300 $$NAME.png; \
	done; \

images:
	for FILE in *.tex; do \
		NAME=`basename $$FILE .tex`; \
		for EXT in $(IMAGEEXT); do \
			convert -trim -bordercolor white -density 300 $$NAME.pdf -quality 300 $$NAME.$$EXT; \
		done; \
	done; \

clean: clean-temp clean-output

clean-temp:
	rm *~; \
	rm *.log; \
	rm *.aux; \

clean-output:
	rm *.png; \
	rm *.jpg; \
	rm *.pdf; \
