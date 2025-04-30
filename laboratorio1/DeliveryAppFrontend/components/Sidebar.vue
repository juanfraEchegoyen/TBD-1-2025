<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Icon } from '@iconify/vue'
import { useRoute } from 'vue-router'
import { menuOptions } from '@/utils/const'

const route = useRoute()
const options = ref(menuOptions)

const isExpanded = ref(true)
const hoveredOption = ref(null)
let leaveTimeout = null

const submenuRef = ref(null)
const submenuPosition = ref({ top: '0px', left: '0px', bottom: 'auto', maxHeight: 'auto' })

const handleMouseEnter = async (index, event) => {
  if (leaveTimeout) clearTimeout(leaveTimeout)
  hoveredOption.value = index

  await nextTick()

  if (submenuRef.value) {
    const rect = event.target.getBoundingClientRect()
    const submenuHeight = submenuRef.value.offsetHeight
    const viewportHeight = window.innerHeight
    const spaceBelow = viewportHeight - rect.bottom
    const spaceAbove = rect.top

    let top, bottom, left, maxHeight

    left = `${rect.right + 5}px`

    if (submenuHeight <= spaceBelow) {
      top = `${rect.top}px`
      bottom = 'auto'
      maxHeight = 'auto'
    } else if (submenuHeight <= spaceAbove) {
      top = 'auto'
      bottom = `${viewportHeight - rect.top}px`
      maxHeight = 'auto'
    } else {
      if (spaceBelow >= spaceAbove) {
        top = `${rect.top}px`
        bottom = 'auto'
        maxHeight = `${Math.min(spaceBelow, viewportHeight * 0.9)}px`
      } else {
        top = 'auto'
        maxHeight = `${Math.min(spaceAbove, viewportHeight * 0.9)}px`
        bottom = `${viewportHeight - rect.bottom}px`
      }
    }

    submenuPosition.value = { top, bottom, left, maxHeight }
  }
}

const handleMouseLeave = () => {
  leaveTimeout = setTimeout(() => {
    hoveredOption.value = null
  }, 100)
}

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
  localStorage.setItem('expanded', isExpanded.value)
}

onMounted(() => {
  document.title = 'Altiro Delivery'
  const expanded = localStorage.getItem('expanded')
  isExpanded.value = expanded === 'true'
})
</script>

<template>
  <nav
    class="relative top-0 left-0 h-screen max-h-screen shadow-lg transition-all duration-300 overflow-y-auto bg-primary"
    :class="isExpanded ? 'w-64' : 'w-20'">

    <nuxt-link to="/" class="px-1 py-3 block">
      <img :src="'/assets/imgs/altiro-logo.png'"
        alt="Logo"
        :class="isExpanded ? 'h-300 mx-auto transition-all duration-300' : 'h-20 mx-auto transition-all duration-300'" />
    </nuxt-link>

    <!-- Botón expandir/contraer -->
    <button @click="toggleExpanded" class="w-full flex py-2 justify-center hover:bg-white/10 transition-colors">
      <Icon :icon="isExpanded ? 'bx:arrow-to-left' : 'bx:arrow-from-left'" class="h-6 w-6 text-white" />
    </button>

    <hr class="border-t border-white/30 my-2 mx-4" />

    <!-- Opciones del menú principal -->
   <div class="flex flex-col space-y-1 px-2 mb-2">
      <div v-for="(option, index) in options" :key="option.title" class="relative"
        @mouseenter="(event) => handleMouseEnter(index, event)" @mouseleave="handleMouseLeave">

        <nuxt-link :to="option.link" class="group flex items-center p-3 rounded-lg transition-colors duration-200"
          :class="[
            option.active ? 'bg-secondary text-primary' : 'hover:bg-tertiary hover:text-secondary',
            hoveredOption === index ? 'bg-tertiary *:text-secondary' : '',
            isExpanded ? 'justify-start' : 'justify-center'
          ]">
          <Icon :icon="option.icon"
            class="h-6 w-6 text-secondary group-hover:text-primary transition-colors duration-200" />
          <span v-if="isExpanded"
            class="ml-3 text-secondary group-hover:text-primary font-medium text-xl transition-colors duration-200">
            {{ option.title }}
          </span>
        </nuxt-link>

        <div v-if="option.submenu && option.submenu.length" v-show="hoveredOption === index" ref="submenuRef"
          class="fixed bg-white text-black rounded-lg shadow-lg transition-opacity duration-200 z-50 overflow-y-auto"
          :style="{
            left: submenuPosition.left,
            top: submenuPosition.top,
            bottom: submenuPosition.bottom,
            maxHeight: submenuPosition.maxHeight,
            width: '250px'
          }">

          <div class="flex flex-col relative">
            <nuxt-link v-for="suboption in option.submenu" :key="suboption.title" :to="suboption.link" 
            class="block px-4 pt-2 relative font-bold transition-colors hover:bg-primary hover:text-white
            after:content-[''] after:block after:w-full after:h-[1px] after:bg-gray-300 after:mx-auto
            after:mt-2 last:after:hidden last:pb-2 text-pretty">
              {{ suboption.title }}
            </nuxt-link>

          </div>
        </div>

      </div>
    </div>

    <hr class="border-t border-white/30 my-2 mx-4 mt-auto" />

    <nuxt-link to="/login"
      class="group flex items-center p-3 mx-2 rounded-lg transition-colors duration-200 mb-2 hover:bg-white"
      :class="isExpanded ? 'justify-start' : 'justify-center'">
      <Icon icon="bx:bxs-user" class="h-6 w-6 text-white transition-colors duration-200 group-hover:text-primary" />
      <span v-if="isExpanded"
        class="ml-3 text-white group-hover:text-primary font-medium text-xl transition-colors duration-200">
        Login
      </span>
    </nuxt-link>

    <nuxt-link to="/logout"
      class="group flex items-center p-3 mx-2 rounded-lg transition-colors duration-200 mb-4 hover:bg-white"
      :class="isExpanded ? 'justify-start' : 'justify-center'">
      <Icon icon="bx:bx-log-out" class="h-6 w-6 text-white transition-colors duration-200 group-hover:text-primary" />
      <span v-if="isExpanded"
        class="ml-3 text-white group-hover:text-primary font-medium text-xl transition-colors duration-200">
        Logout
      </span>
    </nuxt-link>
  </nav>
</template>
