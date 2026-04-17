import api from "./client.js";

export async function getSummary(year) {
  const { data } = await api.get("/analytics/summary", { params: { year } });
  return data;
}
