# Use an official Node.js runtime as a parent image
FROM node:18-alpine AS base

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock)
COPY package*.json ./
# Or if using yarn:
# COPY package.json yarn.lock ./

# Install app dependencies
# Using --force might hide underlying dependency issues. Consider resolving them.
RUN npm install --force
# Or if using yarn:
# RUN yarn install --frozen-lockfile

# Copy the rest of the application code
COPY . .

# Build the Next.js application
RUN npm run build
# Or if using yarn:
# RUN yarn build

# --- Runner Stage ---
FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV production
# Uncomment the following line in case you disable telemetry during build stage.
# ENV NEXT_TELEMETRY_DISABLED 1

# Copy necessary files from the build stage
COPY --from=base /app/public ./public
COPY --from=base /app/.next ./.next
COPY --from=base /app/node_modules ./node_modules
COPY --from=base /app/package.json ./package.json
# If using yarn, copy yarn.lock as well
# COPY --from=base /app/yarn.lock ./yarn.lock

# Expose the port the app runs on
EXPOSE 3000

# Define the command to run the app in production
CMD ["npm", "start"]
# Or if using yarn:
# CMD ["yarn", "start"]
