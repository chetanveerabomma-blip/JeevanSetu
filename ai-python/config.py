import os

class Config:
    HOST: str = os.getenv("AI_HOST", "0.0.0.0")
    PORT: int = int(os.getenv("AI_PORT", "8000"))
    SERVICE_NAME: str = "JeevanSetu-AI-Intelligence-Engine"
    VERSION: str = "2.0.0"

config = Config()
