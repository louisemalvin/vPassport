FROM node:14-alpine
WORKDIR /app
COPY package.json package-lock.json ./
ENV PATH /app/node_modules/.bin:$PATH
RUN npm install
COPY . .
RUN npm run build
CMD npm start
