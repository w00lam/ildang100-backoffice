DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS history;
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

CREATE TABLE history (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         admin_id BIGINT NOT NULL,
                         status VARCHAR(30) NOT NULL,
                         reject_reason VARCHAR(100),
                         rejected_at DATETIME,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         CONSTRAINT fk_history_admin
                             FOREIGN KEY (admin_id) REFERENCES admins(id)
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
                          deletion_status VARCHAR(20) NOT NULL DEFAULT 'NOT_DELETED',  -- ⬅ 추가 (P-6)
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL,
                          CONSTRAINT fk_products_admin
                              FOREIGN KEY (admin_id) REFERENCES admins(id)
);
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        admin_id BIGINT,
                        customer_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        order_number BIGINT NOT NULL,
                        unit_price INT NOT NULL,
                        total_price INT NOT NULL,
                        cancel_reason VARCHAR(255),
                        CONSTRAINT uk_orders_order_number
                            UNIQUE (order_number),
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
                         order_id BIGINT NOT NULL,                                       -- ⬅ 추가 (Review entity 정합)
                         rating INT NOT NULL,
                         content VARCHAR(500) NOT NULL,                                  -- ⬅ 변경 255 → 500 (entity 정합)
                         deletion_status VARCHAR(20) NOT NULL DEFAULT 'NOT_DELETED',     -- ⬅ 추가 (R-3)
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         CONSTRAINT fk_reviews_customer
                             FOREIGN KEY (customer_id) REFERENCES customers(id),
                         CONSTRAINT fk_reviews_product
                             FOREIGN KEY (product_id) REFERENCES products(id),
                         CONSTRAINT fk_reviews_order
                             FOREIGN KEY (order_id) REFERENCES orders(id)               -- ⬅ 추가
);
