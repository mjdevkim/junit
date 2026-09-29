INSERT INTO users (email, name, status)
VALUES
    ('a@test.com', 'Alice', 'ACTIVE'),
    ('b@test.com', 'Bob', 'INACTIVE');

INSERT INTO menu (name, price, user_id)
VALUES
    ('Pizza', 12000, 1),
    ('Burger', 8000, 1),
    ('Pasta', 15000, 2);