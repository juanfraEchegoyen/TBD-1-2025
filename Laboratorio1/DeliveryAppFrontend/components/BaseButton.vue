<template>
  <button 
    :class="[
      'px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors', 
      $attrs.class
    ]"
    v-bind="$attrs"
  >
    <slot></slot>
  </button>
</template>

<script>
export default {
  name: 'BaseButton',
  inheritAttrs: false
}
</script>