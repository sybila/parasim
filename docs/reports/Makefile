TO_CLEAN=aux nav log blg bib~ sty~ bbl tex~ out toc snm idx dvi txt~ idx log
MILLSTONES=1.0.0.M1

.PHONY: report
report:
	for MILLSTONE in $(MILLSTONES); do pdflatex $$MILLSTONE.tex; done;

.PHONY: clean
clean:
	rm -rf Makefile~;
	for EXT in $(TO_CLEAN); do rm -rf *.$$EXT; done;
