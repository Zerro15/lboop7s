const mongoose = require('mongoose');

/**
 * Establish a MongoDB connection using mongoose.
 * Exits the process when a connection cannot be established so container orchestrators can restart.
 */
const connectDB = async () => {
  const mongoUri = process.env.MONGODB_URI || 'mongodb://127.0.0.1:27017/auth_app';

  try {
    await mongoose.connect(mongoUri);
    console.log(`MongoDB connected: ${mongoUri}`);
  } catch (err) {
    console.error('MongoDB connection error:', err.message);
    process.exit(1);
  }

  mongoose.connection.on('error', (err) => {
    console.error('MongoDB runtime error:', err);
  });
};

module.exports = connectDB;
