export default defineEventHandler(async (event) => {
  const students = Array.from({ length: 10 }, generateFakeStudent)
  return students
})