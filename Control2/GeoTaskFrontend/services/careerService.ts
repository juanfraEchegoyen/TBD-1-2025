import { useRuntimeConfig } from '#app'
import { type Career } from '~/types/types'

export default class CarrersService {
  private baseCareersURL: string

  constructor() {
    const config = useRuntimeConfig()
    this.baseCareersURL = `${config.public.apiBase}/api/careers`
  }

  async getCareers() {
    try {
      return await $fetch<Career[]>(`${this.baseCareersURL}`, {
        params: {
          includeDepartment: 'true'
        }
      })
    } catch (error) {
      console.error('Error fetching carrers:', error)
      throw error
    }
  }

  async getCareerById(id: string) {
    try {
      return await $fetch<Career>(`${this.baseCareersURL}/${id}`, {
        params: {
          includeDepartment: 'true'
        }
      })
    } catch (error) {
      console.error(`Error fetching carrer with id ${id}:`, error)
      throw error
    }
  }
}