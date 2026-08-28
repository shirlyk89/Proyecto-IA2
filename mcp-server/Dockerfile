FROM node:20-slim
WORKDIR /app
COPY package.json .
RUN npm install --omit=dev
COPY src ./src
EXPOSE 3939
CMD ["node", "src/http.js"]