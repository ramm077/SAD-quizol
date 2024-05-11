import React from 'react'
import loadingGif from '../../images/loading.gif'
import { Modal } from '@mui/material'

const LoadingModal = ({visible}) => {

  console.log(visible)

  return (
    <div>
      <Modal
        open={visible}
      >
        <img src={loadingGif} alt='loading gif' />
      </Modal>
    </div>
  )
}

export default LoadingModal