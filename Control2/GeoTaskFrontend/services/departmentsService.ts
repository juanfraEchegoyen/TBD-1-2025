import { useRuntimeConfig } from "#app";
import { type Career, type Department } from "~/types/types";

export default class DepartmentsService {
  private baseDepartmentURL: string;

  constructor() {
    const config = useRuntimeConfig();
    this.baseDepartmentURL = `${config.public.apiBase}/api/departments`;
  }

  async getDepartments() {
    try {
      return await $fetch<Department[]>(`${this.baseDepartmentURL}`, {
        params: {
          includeCareers: "true",
          includeFaculty: "true",
        },
      });
    } catch (error) {
      console.error("Error fetching carrers:", error);
      throw error;
    }
  }

  async getDepartmentById(id: string) {
    try {
      return await $fetch<Department>(`${this.baseDepartmentURL}/${id}`, {
        params: {
          includeCareers: "true",
          includeFaculty: "true",
        },
      });
    } catch (error) {
      console.error(`Error fetching carrer with id ${id}:`, error);
      throw error;
    }
  }

  async getCarrersByDepartmentId(id: string) {
    try {
      return await $fetch<Career>(`${this.baseDepartmentURL}/${id}/careers`, {
        params: {
          includeFaculty: "true",
        },
      });
    } catch (error) {
      console.error(`Error fetching carrers for department with id ${id}:`, error);
      throw error;
    }
  }
}
