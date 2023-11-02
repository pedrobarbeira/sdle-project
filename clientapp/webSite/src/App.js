import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { useState } from 'react';

import './App.css';
import ShoppingItems from './Components/shoppingItems';
import ShoppingLists from './Components/shoppingLists';
import NavBar from './Components/navBar';


function App() {
  const [loggedIn, setLoggedIn] = useState(false);

  return (
    <div className="App">
      <NavBar loggedIn={loggedIn} setLoggedin={setLoggedIn}/>
      
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<ShoppingLists loggedIn={loggedIn} />} />
          <Route path="/shopping-items/:id" element={<ShoppingItems loggedIn={loggedIn} />} />
        </Routes>
      </BrowserRouter>
    
    </div>
  );
}

export default App;
