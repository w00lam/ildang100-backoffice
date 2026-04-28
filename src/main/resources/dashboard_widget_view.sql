CREATE VIEW dashboard_widget_view AS
SELECT
    1 AS id,

    COALESCE((
        SELECT SUM(o.total_price)
        FROM orders o
    ), 0) AS total_sales,

    COALESCE((
        SELECT SUM(o.total_price)
        FROM orders o
        WHERE DATE(o.created_at) = CURRENT_DATE
    ), 0) AS today_sales,

    (
        SELECT COUNT(*)
        FROM orders o
        WHERE o.status = 'PREPARING'
    ) AS preparing_order_count,

    (
        SELECT COUNT(*)
        FROM orders o
        WHERE o.status = 'SHIPPING'
    ) AS shipping_order_count,

    (
        SELECT COUNT(*)
        FROM orders o
        WHERE o.status = 'DELIVERED'
    ) AS delivered_order_count,

    (
        SELECT COUNT(*)
        FROM products p
        WHERE p.stock <= 5
          AND p.stock > 0
    ) AS low_stock_product_count,

    (
        SELECT COUNT(*)
        FROM products p
        WHERE p.stock = 0
           OR p.status = 'OUT_OF_STOCK'
    ) AS out_of_stock_product_count;