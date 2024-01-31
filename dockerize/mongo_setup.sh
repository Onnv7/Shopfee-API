#!/bin/bash
echo "sleeping for 10 seconds"
# sleep 12

echo mongo_setup.sh time now: `date +"%T" `
mongosh <<EOF
  var cfg = {
    "_id": "rs0",
    "version": 1,
    "members": [
      {
        "_id": 0,
        "host": "mongo1:27017",
        "priority": 1
      },
      {
        "_id": 1,
        "host": "mongo2:27017",
        "priority": 0
      }
    ]
  };
  rs.initiate(cfg);
EOF
