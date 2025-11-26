 
//import './App.css'
import React, { useContext, useEffect, useState } from "react";
import { useDispatch } from "react-redux";

import { Box, Button } from "@mui/material";
import { AuthContext } from "react-oauth2-code-pkce";
import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router-dom";
import { logout, setCredentials } from "./store/authSlice";
import ActivityForm from "./store/components/ActivityForm";
import ActivityDetail from "./store/components/ActivityDetail";
import ActivityList from "./store/components/ActivityList";



const ActivitiesPage = () => {
  return(
    <Box sx={{p:2, border:'1px dashed grey'}}>
      <ActivityForm onActivityAdded={() => window.location.reload()} />
      <ActivityList />
    </Box>
  );
}


function App() {
 const {token, tokenData,logIn, logOut, isAuthenticated}
        =useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);
  useEffect(() => {
    if (token){
      dispatch(setCredentials({token, user: tokenData}));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch]);
   
  return (
    <Router>
      {!token ? (
        <Button variant="contained" onClick={() => {logIn();}}>

            LOGIN
          </Button>
      ) : (
        <div>
          <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
          <Button variant="contained" onClick={logout}>
            LOGOUT
          </Button>
          <Routes>
            <Route path="/activities" element={<ActivitiesPage />} />
            <Route path="/activities/:id" element={<ActivityDetail />} />
            <Route path="/" element={token ? <Navigate to="/activities" replace /> : <div>Home - User is not logged in</div>} />
          </Routes>
          </Box>
        </div>
      )}
      
        
    </Router>
  )
}

export default App
