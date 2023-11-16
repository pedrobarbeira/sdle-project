// ShoppingItemsPage.js
import React, { useEffect, useState } from 'react';
import { Button, Card, Container, Form, Row, Col } from 'react-bootstrap';
import { useParams } from 'react-router-dom';

const ShoppingItems = ({ loggedIn }) => {
  const { id } = useParams();

  const [newItem, setNewItem] = useState({ name: '', type: 'boolean', quantity: 0 });
  
  const [items, setItems] = useState([
    { id: 1, name: 'Item 1', type: 'boolean', quantity: 0 },
    { id: 2, name: 'Item 2', type: 'boolean', quantity: 1 },
    { id: 3, name: 'Item 3', type: 'quantity', quantity: 5 },
  ]);

  useEffect(() => {
    console.log(items)
  }, [items])

  const handleNewItemChange = (e) => {
    const { name, value, type } = e.target;

    setNewItem((prevItem) => ({
      ...prevItem,
      [name]: type === 'number' ? parseInt(value, 10) : (type === 'checkbox' ? (prevItem.quantity + 1) % 2 : value),
    }));
  };

  const handleAddItem = () => {
    const newItemWithId = { ...newItem, id: items.length + 1 };
    
    setItems([...items, newItemWithId]);
    setNewItem({ name: '', type: 'boolean', quantity: 0 });
  };
  
  const handleRemoveItem = (itemId) => {
    setItems(items.filter((item) => item.id !== itemId));
  };

  const handleUpdateItemName = (itemId, newName) => {
    setItems((prevItems) =>
      prevItems.map((prevItem) =>
        prevItem.id === itemId
          ? { ...prevItem, name: newName }
          : prevItem
      )
    );
  };

  const handleSave = () => {
    console.log('Items to save:', items);
  };

  return (
    <Container className="mt-5">
      <Row className="mb-3">
        <Col xs={12} md={6}>
          <h1>Shopping Items Page {id}</h1>
        </Col>
        <Col xs={12} md={6} className="d-flex justify-content-end">
          <Button variant="success" onClick={handleSave} style={{ marginTop: 'auto' }}>
            Save
          </Button>
        </Col>
      </Row>
      <Form>
        
        <Row className="mb-3">
          <Col xs={12} md={4}>
            <Form.Group>
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={newItem.name}
                onChange={handleNewItemChange}
                placeholder="New Item Name"
              />
            </Form.Group>
          </Col>
          <Col xs={12} md={4}>
            <Form.Group>
              <Form.Label>Type</Form.Label>
              <Form.Control
                as="select"
                name="type"
                value={newItem.type}
                onChange={handleNewItemChange}
              >
                <option value="boolean">Unchecked/Checked</option>
                <option value="quantity">Quantity</option>
              </Form.Control>
            </Form.Group>
          </Col>
          <Col xs={12} md={2}>
          {newItem.type === 'boolean' ? (
            <Form.Group>
              <Form.Label>Checked</Form.Label>
              <Form.Check
                type="checkbox"
                label=""
                name="quantity"
                checked={newItem.quantity !== null && newItem.quantity > 0}
                onChange={handleNewItemChange}
              />
            </Form.Group>
          ) : (
            <Form.Group>
              <Form.Label>Quantity</Form.Label>
              <Form.Control
                type="number"
                name="quantity"
                value={newItem.quantity || ''}
                onChange={handleNewItemChange}
              />
            </Form.Group>
          )}
          </Col>
          <Col xs={12} md={2}>
            <Button variant="primary" onClick={handleAddItem} style={{ marginTop: '2rem' }}>
              Add Item
            </Button>
          </Col>
        </Row>

        {items.map((item) => (
          <Card key={item.id} className="mb-3">
            <Card.Body>
              <Row>
                <Col xs={12} md={4}>
                  <Form.Group>
                    <Form.Label>Name</Form.Label>
                    <Form.Control
                      type="text"
                      name="name"
                      value={item.name}
                      onChange={(e) => handleUpdateItemName(item.id, e.target.value)}
                    />
                  </Form.Group>
                </Col>
                <Col xs={12} md={4}>
                {item.type === 'boolean' ? (
                  <Form.Group>
                    <Form.Check
                      type="checkbox"
                      label="Checked"
                      checked={item.quantity}
                      onChange={(e) => {
                        const isChecked = e.target.checked;
                        setItems((prevItems) =>
                          prevItems.map((prevItem) =>
                            prevItem.id === item.id
                              ? { ...prevItem, quantity: isChecked ? 1:0 }
                              : prevItem
                          )
                        );
                      }}
                    />
                  </Form.Group>
                ) : (
                  <Form.Group>
                    <Form.Label>Quantity</Form.Label>
                    <Form.Control
                      type="number"
                      value={item.quantity || ''}
                      onChange={(e) => {
                        const newQuantity = parseInt(e.target.value, 10) || null;
                        setItems((prevItems) =>
                          prevItems.map((prevItem) =>
                            prevItem.id === item.id
                              ? { ...prevItem, quantity: newQuantity }
                              : prevItem
                          )
                        );
                      }}
                    />
                  </Form.Group>
                )}
                </Col>
                <Col xs={12} md={4}>
                  <Button variant="danger" onClick={() => handleRemoveItem(item.id)}>
                    Remove
                  </Button>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        ))}
      </Form>
    </Container>
  );
}

export default ShoppingItems;