import api from "./client.js";

export async function listUsers() {
  const { data } = await api.get("/admin/users");
  return data;
}

export async function createUser(payload) {
  const { data } = await api.post("/admin/users", payload);
  return data;
}

export async function getPolicy() {
  const { data } = await api.get("/policy");
  return data;
}

export async function updatePolicy(payload) {
  const { data } = await api.put("/policy", payload);
  return data;
}

export async function getLeavePredictions() {
  const { data } = await api.post("/admin/ml/predict");
  return data;
}
