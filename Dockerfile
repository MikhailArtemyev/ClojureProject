FROM clojure:tools-deps
LABEL authors="michaelartemyev"
WORKDIR /app
COPY deps.edn .
COPY src/core ./src
RUN clojure -P
CMD ["clojure", "-M:run"]