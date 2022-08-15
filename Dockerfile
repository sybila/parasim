# Download base image ubuntu 20.04
FROM ubuntu:16.04
MAINTAINER Matej Trojak <xtrojak@fi.muni.cz>

# Install dependencies
RUN apt-get update
RUN apt-get install -y wget software-properties-common

# Install Octave
RUN apt-add-repository ppa:octave/stable
RUN apt-get update
RUN apt-get install -y octave

# Install miniconda
RUN wget --quiet https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O ~/miniconda.sh
RUN /bin/bash ~/miniconda.sh -b -p /opt/conda
ENV PATH="/opt/conda/bin:$PATH"

# Install Java
RUN conda install -c conda-forge openjdk=8.0.332

# Install Parasim
COPY . parasim/
WORKDIR parasim
RUN ./gradlew installDist
ENV PATH="/parasim/application/build/install/parasim/bin:$PATH"