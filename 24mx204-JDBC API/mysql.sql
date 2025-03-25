CREATE DATABASE IF NOT EXISTS bookstore;
USE bookstore;

CREATE TABLE authors (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(255) NOT NULL
);

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    author_id INT,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);

-- Insert authors
INSERT INTO authors (author_name) VALUES 
('J.K. Rowling'),
('George Orwell'),
('J.R.R. Tolkien'),
('Mark Twain');

-- Insert books
INSERT INTO books (title, price, author_id) VALUES 
('Harry Potter', 499.99, 1),
('1984', 299.99, 2),
('The Hobbit', 399.99, 3),
('Adventures of Huckleberry Finn', 250.00, 4);

INSERT INTO books (title, price, author_id) 
VALUES ('SQL for Beginners', 379.99, 4);

SELECT * FROM books 
WHERE author_id = 4;

UPDATE books 
SET price = 299.99 
WHERE book_id = 2;

DELETE FROM books 
WHERE book_id = 17;

SELECT b.title, a.author_name, b.price 
FROM books b 
JOIN authors a ON b.author_id = a.author_id;

SELECT a.author_name, b.title, b.price 
FROM authors a 
LEFT JOIN books b ON a.author_id = b.author_id;

SELECT * FROM books ORDER BY price DESC;







