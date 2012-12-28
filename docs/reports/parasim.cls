\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{parasim}[2012/07/06 Class for documents concerning Parasim -- tool for paralel simulation and verification (in Czech)]

\LoadClass{article}
\RequirePackage[utf8]{inputenc} % input encoding
\RequirePackage[czech]{babel}   % language settings
\RequirePackage{a4wide}         % margin adjustment
\RequirePackage{amssymb}        % AMS symbols
\RequirePackage{multicol}       % multiple columns
\RequirePackage{amsmath}        % AMS mathematics extension
\RequirePackage{enumerate}      % enumerate customization
%\RequirePackage{algorithmic}    % typesetting for algorithms
%\RequirePackage{algorithm}      % algorithm environment
\RequirePackage{url}			% adding URLs
\RequirePackage{hyperref}		% hypertext
\RequirePackage{color}			% colors
\RequirePackage{ifthen}         % conditions

%color definition
\definecolor{dkgreen}{rgb}{0,0.6,0}
\definecolor{gray}{rgb}{0.5,0.5,0.5}
\definecolor{mauve}{rgb}{0.58,0,0.82}

\newcommand{\parasim@header}{
	\begin{center}
		\begin{minipage}{0.69\textwidth}
			\begin{flushleft}
				\textbf{Nástroj pro paralelní simulace a verifikaci} \\
				\url{https://github.com/sybila/parasim}
			\end{flushleft}
		\end{minipage}    
		\begin{minipage}{0.29\textwidth}
			\begin{flushright} 
				Jan Papoušek (325494)       \\
				Tomáš Vejpustek (324713)
			\end{flushright}
		\end{minipage}

		\hrulefill
	\end{center}
}

\newcommand{\parasim@titlebox}{}
\renewcommand{\title}[1]{\renewcommand{\parasim@titlebox}{#1}}
\newcommand{\parasim@title}{\ifthenelse{\equal{\parasim@titlebox}{}}{}{
	\begin{center}
	\LARGE\bf \parasim@titlebox
	\end{center}
}}

\AtBeginDocument{
\parasim@header
\parasim@title
}
