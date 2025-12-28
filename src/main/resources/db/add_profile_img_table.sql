-- Migration: Add profile_picture_blob to users table
-- Date: 2025-12-28
-- Description: Adds BLOB column to users table for storing profile pictures (owner and carrier)

-- Add profile_picture_blob column to users table
ALTER TABLE `users` 
ADD COLUMN `profile_picture_blob` BLOB NULL AFTER `role`;

-- Usage: Run this SQL in your phpMyAdmin to add profile picture support
-- The column is nullable, so existing users won't be affected
