const express = require('express');
const { body, validationResult } = require('express-validator');
const jwt = require('jsonwebtoken');
const User = require('../models/User');

const router = express.Router();

const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '7d';

const buildValidationErrorResponse = (errors) =>
  errors.array().map((err) => ({ field: err.param, message: err.msg }));

router.post(
  '/register',
  [
    body('email').isEmail().withMessage('Некорректный email').normalizeEmail(),
    body('password')
      .isLength({ min: 6 })
      .withMessage('Пароль должен содержать минимум 6 символов'),
  ],
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: 'Некорректные данные', errors: buildValidationErrorResponse(errors) });
    }

    const { email, password } = req.body;

    try {
      const existingUser = await User.findOne({ email });
      if (existingUser) {
        return res.status(400).json({ message: 'Пользователь с таким email уже существует' });
      }

      const user = new User({ email, password });
      await user.save();

      const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
      res.cookie('token', token, {
        httpOnly: true,
        sameSite: 'lax',
        secure: process.env.NODE_ENV === 'production',
        maxAge: 7 * 24 * 60 * 60 * 1000,
      });

      return res.status(201).json({ message: 'Пользователь успешно зарегистрирован', user: user.toJSON(), token });
    } catch (err) {
      console.error('Registration error:', err);
      return res.status(500).json({ message: 'Не удалось завершить регистрацию' });
    }
  }
);

router.post(
  '/login',
  [
    body('email').isEmail().withMessage('Некорректный email').normalizeEmail(),
    body('password').notEmpty().withMessage('Пароль обязателен'),
  ],
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: 'Некорректные данные', errors: buildValidationErrorResponse(errors) });
    }

    const { email, password } = req.body;

    try {
      const user = await User.findOne({ email });
      if (!user) {
        return res.status(401).json({ message: 'Неверный логин или пароль' });
      }

      const isMatch = await user.comparePassword(password);
      if (!isMatch) {
        return res.status(401).json({ message: 'Неверный логин или пароль' });
      }

      const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
      res.cookie('token', token, {
        httpOnly: true,
        sameSite: 'lax',
        secure: process.env.NODE_ENV === 'production',
        maxAge: 7 * 24 * 60 * 60 * 1000,
      });

      return res.status(200).json({ message: 'Успешный вход', user: user.toJSON(), token });
    } catch (err) {
      console.error('Login error:', err);
      return res.status(500).json({ message: 'Не удалось выполнить вход' });
    }
  }
);

module.exports = router;
