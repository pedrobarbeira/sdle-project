import React from "react"
import { useEffect, useState } from 'react';
import ShoppingList from './shoppingList';

const ShoppingLists = ({ loggedIn }) => {
  const [shoppingLists, setShoppingLists] = useState([]);

  useEffect(() => {
    if (loggedIn) {
      const newShoppingLists = [
        { title: 'Groceries', totalItems: 10, id: 1 },
        { title: 'Home Supplies', totalItems: 5, id: 2 },
        { title: 'Groceries', totalItems: 10, id: 3 },
        { title: 'Home Supplies', totalItems: 5, id: 4 },
        { title: 'Groceries', totalItems: 10, id: 5 },
        { title: 'Home Supplies', totalItems: 5, id: 6 },
        { title: 'Groceries', totalItems: 10, id: 7 },
        { title: 'Home Supplies', totalItems: 5, id: 8 },
        // Add more shopping lists as needed
      ];
      setShoppingLists(newShoppingLists); // Update shoppingLists state
    } else {
      setShoppingLists([]); // Clear the shoppingLists state
    }
  }, [loggedIn]);

  return (
      <div className="container mt-5 center">
      <div className="row">
        {shoppingLists.map((list, index) => (
          <div className="col-12 col-sm-6 col-md-4 mb-4" key={index}>
            <ShoppingList title={list.title} totalItems={list.totalItems} listId={list.id} />
          </div>
        ))}
      </div>
    </div>
  );
}

export default ShoppingLists