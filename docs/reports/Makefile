TO_CLEAN=aux nav log blg bib~ sty~ bbl tex~ out toc snm idx dvi txt~ idx log
MILLSTONES=1.0.0.M1 1.0.0.M2 1.0.0.M2-3.tex

.PHONY: report
report:
	for MILLSTONE in $(MILLSTONES); do pdflatex $$MILLSTONE.tex; done;

.PHONY: clean
clean:
	rm -rf Makefile~;
	for EXT in $(TO_CLEAN); do rm -rf *.$$EXT; done;
