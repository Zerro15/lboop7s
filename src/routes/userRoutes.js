const express = require('express');
const authMiddleware = require('../middleware/authMiddleware');
const User = require('../models/User');

const router = express.Router();

router.get('/me', authMiddleware, async (req, res) => {
  try {
    const user = await User.findById(req.user.id).select('-password');
    if (!user) {
      return res.status(404).json({ message: 'Пользователь не найден' });
    }

    return res.status(200).json({ user });
  } catch (err) {
    console.error('Fetch current user error:', err);
    return res.status(500).json({ message: 'Не удалось получить данные пользователя' });
  }
});

module.exports = router;
