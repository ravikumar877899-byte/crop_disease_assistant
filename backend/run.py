import os
import platform
import collections

# WMI workaround for Windows Python 3.12+ (prevents hanging imports like sqlalchemy)
platform.machine = lambda: 'AMD64'
platform.architecture = lambda: ('64bit', 'WindowsPE')  # type: ignore
platform.system = lambda: 'Windows'
platform.release = lambda: '10'
try:
    uname_result = collections.namedtuple('uname_result', ['system', 'node', 'release', 'version', 'machine', 'processor'])
    platform.uname = lambda: uname_result('Windows', 'PC', '10', '10.0', 'AMD64', 'AMD64')  # type: ignore
except Exception:
    pass

from api import create_app # type: ignore

app = create_app()

if __name__ == '__main__':
    # Running the modular production API architecture with dynamic port binding for Render/Cloud
    port = int(os.environ.get('PORT', 5000))
    app.run(debug=False, host='0.0.0.0', port=port)
