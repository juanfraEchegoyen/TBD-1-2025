export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const withDepartments = query.includeDepartments === 'true'
  
  return Array.from(
    { length: 5 }, 
    () => generateFakeFaculty(withDepartments)
  )
})