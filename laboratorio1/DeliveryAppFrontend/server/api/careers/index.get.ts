export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const limit = Number(query.limit) || 10
  
  const careersWithRelations = Array.from(
    { length: limit }, 
    generateCareerWithRelations
  )
  
  return careersWithRelations
})