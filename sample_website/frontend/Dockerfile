# Use the official Node.js image as the base image
FROM node:14-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json to the container
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the frontend source code to the container
COPY . .

# Build the React app for production
RUN npm run build

# Command to start the development server
CMD ["npm", "start"]
