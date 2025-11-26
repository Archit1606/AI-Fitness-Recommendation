import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice";
export const store = configureStore({
  reducer: {
    // your reducers go here
    auth: authReducer,
  },
});