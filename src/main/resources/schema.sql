DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS admins;

CREATE TABLE admins (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(30) NOT NULL,
                        email VARCHAR(50) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        tele VARCHAR(20) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        role VARCHAR(30) NOT NULL,
                        created_at DATETIME NOT NULL,
                        approved_at DATETIME,
                        updated_at DATETIME NOT NULL
);

CREATE TABLE customers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(30) NOT NULL,
                           email VARCHAR(50) NOT NULL,
                           tele VARCHAR(20) NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           created_at DATETIME NOT NULL,
                           updated_at DATETIME NOT NULL
);

CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          admin_id BIGINT NOT NULL,
                          name VARCHAR(30) NOT NULL,
                          category VARCHAR(30) NOT NULL,
                          price INT NOT NULL,
                          stock INT NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL,
                          CONSTRAINT fk_products_admin
                              FOREIGN KEY (admin_id) REFERENCES admins(id)
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        admin_id BIGINT NOT NULL,
                        customer_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        order_number BIGINT NOT NULL,
                        unit_price INT NOT NULL,
                        total_price INT NOT NULL,
                        CONSTRAINT fk_orders_admin
                            FOREIGN KEY (admin_id) REFERENCES admins(id),
                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES customers(id),
                        CONSTRAINT fk_orders_product
                            FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE reviews (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         customer_id BIGINT NOT NULL,
                         product_id BIGINT NOT NULL,
                         rating INT NOT NULL,
                         content VARCHAR(255) NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         CONSTRAINT fk_reviews_customer
                             FOREIGN KEY (customer_id) REFERENCES customers(id),
                         CONSTRAINT fk_reviews_product
                             FOREIGN KEY (product_id) REFERENCES products(id)
);