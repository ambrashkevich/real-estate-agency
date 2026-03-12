-- Functions
CREATE OR REPLACE FUNCTION get_total_deals_by_agent(a_id BIGINT) RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM deals WHERE agent_id = a_id);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_avg_price_in_district(d_id BIGINT) RETURNS DECIMAL AS $$
BEGIN
    RETURN (SELECT AVG(price) FROM properties WHERE district_id = d_id);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION format_currency(amount DECIMAL) RETURNS TEXT AS $$
BEGIN
    RETURN to_char(amount, 'FM999,999,999,999.00') || ' RUB';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_client_deal_count(c_id BIGINT) RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM deals WHERE client_id = c_id);
END;
$$ LANGUAGE plpgsql;

-- Procedures
CREATE OR REPLACE PROCEDURE close_deal(d_id BIGINT) AS $$
BEGIN
    UPDATE deals SET status = 'COMPLETED', deal_date = NOW() WHERE id = d_id;
    UPDATE properties SET status = 'SOLD' WHERE id = (SELECT property_id FROM deals WHERE id = d_id);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE archive_old_properties(days_old INT) AS $$
BEGIN
    UPDATE properties SET status = 'ARCHIVED' 
    WHERE status = 'AVAILABLE' AND updated_at < NOW() - INTERVAL '1 day' * days_old;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE update_agent_commission(a_id BIGINT, new_comm DECIMAL) AS $$
BEGIN
    UPDATE agents SET commission = new_comm WHERE id = a_id;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE OR REPLACE FUNCTION log_property_change() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO audit_logs(entity_name, entity_id, action, username, details, created_at)
    VALUES ('Property', COALESCE(NEW.id, OLD.id), TG_OP, 'SYSTEM', 'Property changed: ' || COALESCE(NEW.title, OLD.title), NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS property_audit_trg ON properties;
CREATE TRIGGER property_audit_trg
AFTER INSERT OR UPDATE OR DELETE ON properties
FOR EACH ROW EXECUTE FUNCTION log_property_change();

CREATE OR REPLACE FUNCTION validate_property_price() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.price < 0 THEN
        RAISE EXCEPTION 'Price cannot be negative';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS property_price_val_trg ON properties;
CREATE TRIGGER property_price_val_trg
BEFORE INSERT OR UPDATE ON properties
FOR EACH ROW EXECUTE FUNCTION validate_property_price();

CREATE OR REPLACE FUNCTION update_deal_status_on_payment() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'COMPLETED' THEN
        UPDATE deals SET status = 'PAID' WHERE id = NEW.deal_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS deal_payment_trg ON payments;
CREATE TRIGGER deal_payment_trg
AFTER UPDATE OF status ON payments
FOR EACH ROW EXECUTE FUNCTION update_deal_status_on_payment();
