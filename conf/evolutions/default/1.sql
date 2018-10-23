# ---!Ups
CREATE TABLE kafka_messages(
  topic_name VARCHAR(255) NOT NULL,
  received_at TIMESTAMP NOT NULL,
  key VARCHAR(63) NOT NULL,
  value TEXT NOT NULL,
  partition INTEGER NOT NULL,
  message_offset REAL NOT NULL,
  PRIMARY KEY (topic_name, key)
);

# --!Downs
DROP TABLE kafka_messages;