<template>
  <button
    class="bg-primary text-white px-6 py-2 rounded-lg hover:bg-red-700 transition-colors"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <slot />
  </button>
</template>