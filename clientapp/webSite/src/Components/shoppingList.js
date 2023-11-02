import React from 'react';
import { Link } from 'react-router-dom';
import { Card, ListGroup } from 'react-bootstrap';

const ShoppingList = ({ title, totalItems, listId }) => {
  return (
    <Link to={`/shopping-items/${listId}`}>
        <Card style={{ width: '18rem' }}>
            <Card.Header>
                {title}
            </Card.Header>
            <ListGroup variant="flush">
                <ListGroup.Item>Total Items: {totalItems}</ListGroup.Item>
            </ListGroup>
        </Card>
    </Link>
  );
};

export default ShoppingList;