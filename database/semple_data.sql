-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 29, 2026 at 10:44 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cookie_kitchen`
--

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
(7, 'Classic'),
(9, 'Gift Boxes'),
(8, 'Premium');

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `category_id`, `name`, `description`, `price`, `image_url`, `stock`, `is_available`, `created_at`) VALUES
(1, 7, 'Chocolate Chip Cookie', 'Classic cookie loaded with chocolate chips', 49.00, 'https://www.alattefood.com/wp-content/uploads/2020/10/Homemade-Chocolate-Chip-Cookies-best-recipe-43-close.jpg', 100, 1, '2026-09-29 08:29:10'),
(2, 7, 'Butter Cookie', 'Simple and buttery, melts in your mouth', 39.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcShwJDc5iH536vd2dy8DsDQMt_Qr1isAG38oU4nKQM6AQ&s=10', 100, 1, '2026-09-29 08:29:10'),
(3, 8, 'Double Choco Fudge', 'If you are serious with chocolate. Than you got to try this', 69.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRSSPMqx3gx-GlJ167Yy9dDcvB7be-STarCnACKzptm5w&s=10', 100, 1, '2026-09-29 08:29:10'),
(4, 8, 'Blueberry Cookie and Choco Chips', 'you know love makes you blue and what else make you blue this.', 89.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSWSt2fVSnjSZPYNU-HNYuhWHLK53aThqpa_5Q7SO5OUg&s=10', 50, 1, '2026-09-29 08:29:10');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
