import React, { useState } from "react";
import {
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import { addActivity } from "../../services/api";

const ActivityForm = ({ onActivityAdded }) => {
  const [activity, setActivity] = useState({
    type: "RUNNING",
    duration: "",
    caloriesBurned: "",
    additionalMetrics: {},
  });


    const handleSubmit = async (e) => {
      e.preventDefault();
      try{
        await addActivity(activity);
        onActivityAdded();
        setActivity({
          type: "RUNNING",
          duration: "",
          caloriesBurned: "",
          additionalMetrics: {},
        });
      }
       catch (error) {
        console.error("Error adding activity:", error);
      }
    };  


  return (
    <Box component="form" sx={{ mb: 2 }} onSubmit={handleSubmit}>
      {/* Activity Type Dropdown */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <InputLabel>Activity Type</InputLabel>
        <Select
          value={activity.type}
          label="Activity Type"
          onChange={(e) =>
            setActivity((prev) => ({ ...prev, type: e.target.value }))
          }
        >
          <MenuItem value="RUNNING">Running</MenuItem>
          <MenuItem value="WALKING">Walking</MenuItem>
          <MenuItem value="CYCLING">Cycling</MenuItem>
        </Select>
      </FormControl>

      {/* Calories Burned Field */}
      <TextField
        fullWidth
        label="Calories Burned"
        type="number"
        sx={{ mb: 2 }}
        value={activity.caloriesBurned}
        onChange={(e) =>
          setActivity((prev) => ({
            ...prev,
            caloriesBurned: e.target.value,
          }))
        }
      />

      {/* Duration Field (optional but useful) */}
      <TextField
        fullWidth
        label="Duration (minutes)"
        type="number"
        sx={{ mb: 2 }}
        value={activity.duration}
        onChange={(e) =>
          setActivity((prev) => ({
            ...prev,
            duration: e.target.value,
          }))
        }
      />
      <Button type='submit' variant="contained">
        Add Activity
      </Button>
    </Box>
  );
};

export default ActivityForm;
